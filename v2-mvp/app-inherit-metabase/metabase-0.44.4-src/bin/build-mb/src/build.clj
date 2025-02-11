(ns build
  (:require [build-drivers :as build-drivers]
            [build.licenses :as license]
            [build.version-info :as version-info]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.build.api :as b]
            [environ.core :as env]
            [flatland.ordered.map :as ordered-map]
            [i18n.create-artifacts :as i18n]
            [metabuild-common.core :as u]))

(defn- edition-from-env-var []
  (case (env/env :mb-edition)
    "oss" :oss
    "ee"  :ee
    nil   :ee))

(defn- build-frontend! [edition]
  {:pre [(#{:oss :ee} edition)]}
  (let [mb-edition (case edition
                     :ee "ee"
                     :oss "oss")]
    (u/step (format "Build frontend with MB_EDITION=%s" mb-edition)
      (u/step "Run 'yarn' to download javascript dependencies"
        (if (env/env :ci)
          (do
            (u/announce "CI run: enforce the lockfile")
            (u/sh {:dir u/project-root-directory} "yarn" "--frozen-lockfile"))
          (u/sh {:dir u/project-root-directory} "yarn")))
      (u/step "Build frontend"
        (u/sh {:dir u/project-root-directory
               :env {"PATH"       (env/env :path)
                     "HOME"       (env/env :user-home)
                     "WEBPACK_BUNDLE"   "production"
                     "MB_EDITION" mb-edition}}
              "yarn" "build"))
      (u/step "Build static viz"
        (u/sh {:dir u/project-root-directory
               :env {"PATH"       (env/env :path)
                     "HOME"       (env/env :user-home)
                     "WEBPACK_BUNDLE"   "production"
                     "MB_EDITION" mb-edition}}
              "yarn" "build-static-viz"))
      (u/announce "Frontend built successfully."))))

(defn- build-licenses!
  [edition]
  {:pre [(#{:oss :ee} edition)]}
  (u/step "Generate backend license information from jar files"
    (let [basis                     (b/create-basis {:project (u/filename u/project-root-directory "deps.edn")})
          output-filename           (u/filename u/project-root-directory
                                                "resources"
                                                "license-backend-third-party.txt")
          {:keys [without-license]} (license/generate {:basis           basis
                                                       :backfill        (edn/read-string
                                                                         (slurp (io/resource "overrides.edn")))
                                                       :output-filename output-filename
                                                       :report?         false})]
      (when (seq without-license)
        (run! (comp (partial u/error "Missing License: %s") first)
              without-license))
      (u/announce "License information generated at %s" output-filename)))

  (u/step "Run `yarn licenses generate-disclaimer`"
    (let [license-text (str/join \newline
                                 (u/sh {:dir    u/project-root-directory
                                        :quiet? true}
                                       "yarn" "licenses" "generate-disclaimer"))]
      (spit (u/filename u/project-root-directory
                        "resources"
                        "license-frontend-third-party.txt") license-text))))

(def uberjar-filename (u/filename u/project-root-directory "target" "uberjar" "metabase.jar"))

(defn- build-uberjar! [edition]
  {:pre [(#{:oss :ee} edition)]}
  (u/delete-file-if-exists! uberjar-filename)
  (u/step (format "Build uberjar with profile %s" edition)
    ;; TODO -- we (probably) don't need to shell out in order to do this anymore, we should be able to do all this
    ;; stuff directly in Clojure land by including this other `build` namespace directly (once we dedupe the names)
    (u/sh {:dir u/project-root-directory} "clojure" "-T:build" "uberjar" :edition edition)
    (u/assert-file-exists uberjar-filename)
    (u/announce "Uberjar built successfully.")))

(def all-steps
  (ordered-map/ordered-map
   :version      (fn [{:keys [edition version]}]
                   (version-info/generate-version-info-file! edition version))
   :translations (fn [_]
                   (i18n/create-all-artifacts!))
   :frontend     (fn [{:keys [edition]}]
                   (build-frontend! edition))
   :licenses     (fn [{:keys [edition]}]
                   (build-licenses! edition))
   :drivers      (fn [{:keys [edition]}]
                   (build-drivers/build-drivers! edition))
   :uberjar      (fn [{:keys [edition]}]
                   (build-uberjar! edition))))

(defn build!
  ([]
   (build! nil))

  ([{:keys [version edition steps]
     :or   {edition :oss
            steps   (keys all-steps)}}]
   (let [version (or version
                     (version-info/current-snapshot-version edition))]
     (u/step (format "Running build steps for %s version %s: %s"
                     (case edition
                       :oss "Community (OSS) Edition"
                       :ee  "Enterprise Edition")
                     version
                     (str/join ", " (map name steps)))
       (doseq [step-name steps
               :let      [step-fn (or (get all-steps (u/parse-as-keyword step-name))
                                      (throw (ex-info (format "Invalid step: %s" step-name)
                                                      {:step        step-name
                                                       :valid-steps (keys all-steps)})))]]
         (step-fn {:version version, :edition edition}))
       (u/announce "All build steps finished.")))))

(defn -main [& steps]
  (u/exit-when-finished-nonzero-on-exception
    (build! (merge {:edition (edition-from-env-var)}
                   (when-let [steps (not-empty steps)]
                     {:steps steps})))))

;; useful to call from command line `cd bin/build-mb && clojure -X build/list-without-license`
(defn list-without-license [{:keys []}]
  (let [[classpath]        (u/sh {:dir    u/project-root-directory
                                           :quiet? true}
                                          "clojure" "-A:ee" "-Spath")
        classpath-entries (license/jar-entries classpath)
        {:keys [without-license]} (license/process*
                                   {:classpath-entries classpath-entries
                                    :backfill        (edn/read-string
                                                        (slurp (io/resource "overrides.edn")))})]
    (if (seq without-license)
      (run! (comp (partial u/error "Missing License: %s") first)
            without-license)
      (u/announce "All dependencies have licenses"))
    (shutdown-agents)
    (System/exit (if (seq without-license) 1 0))))
