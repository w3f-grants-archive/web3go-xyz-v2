/* eslint-disable react/prop-types */
import React from "react";
import { connect } from "react-redux";
import './index.less';
import { Button, Modal, Form, Input, Upload, Carousel } from '@arco-design/web-react';
import { push } from "react-router-redux";
import Cropper from "react-cropper";
import "cropperjs/dist/cropper.css";

const mapStateToProps = state => {
    return {
        isDark: state.app.isDark
    }
};
const mapDispatchToProps = {
    push
};
const FormItem = Form.Item;

class Component extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            slideList: [
                require("@/web3goLayout/assets/home/banner.png"),
                require("@/web3goLayout/assets/home/banner.png"),
                require("@/web3goLayout/assets/home/banner.png"),
                require("@/web3goLayout/assets/home/banner.png"),
            ]
        }
    }

    render() {
        return (
            <div className="web3go-layout-home-page">
                <div className="banner-wrap">
                    <Carousel
                        autoPlay={true}
                        style={{ height: 350 }}
                        indicatorType="line"
                    >
                        {this.state.slideList.map((v, index) => (
                            <div className="slide-item" key={index} style={{ backgroundImage: `url(${v})` }}>
                            </div>
                        ))}
                    </Carousel>
                </div >
                <div className="home-main common-layout">
                    <div className="section-title">
                        <span>My Space</span>
                        <svg className="arrow" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M9.24269 3.75667L13.4854 7.99933L9.24269 12.242M1.66669 8H13.0984" strokeWidth="1.5" />
                        </svg>
                    </div>
                    <div className="section-content info">
                        <div className="info-item hover-item">
                            <div className="i-left">
                                <div className="circle">
                                    <img src={require("@/web3goLayout/assets/home/info1.png")} alt="" />
                                </div>
                                <div className="text-wrap">
                                    <div className="label">Dashboards</div>
                                    <div className="value">800,246</div>
                                </div>
                            </div>
                            <img src={require("@/web3goLayout/assets/home/info11.png")} alt="" />
                        </div>
                        <div className="info-item hover-item">
                            <div className="i-left">
                                <div className="circle">
                                    <img src={require("@/web3goLayout/assets/home/info2.png")} alt="" />
                                </div>
                                <div className="text-wrap">
                                    <div className="label">Datasets</div>
                                    <div className="value">800,246</div>
                                </div>
                            </div>
                            <img src={require("@/web3goLayout/assets/home/info22.png")} alt="" />
                        </div>
                        <div className="info-item hover-item">
                            <div className="i-left">
                                <div className="circle">
                                    <img src={require("@/web3goLayout/assets/home/info3.png")} alt="" />
                                </div>
                                <div className="text-wrap">
                                    <div className="label">My Favorites</div>
                                    <div className="value">800,246</div>
                                </div>
                            </div>
                            <img src={require("@/web3goLayout/assets/home/info33.png")} alt="" />
                        </div>
                        <div className="btn hover-item">
                            <img src={require("@/web3goLayout/assets/home/add.png")} alt="" />
                            <span>New Dashboard</span>
                        </div>
                    </div>
                    <div className="section-title">
                        <span>Dashboard</span>
                        <svg className="arrow" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M9.24269 3.75667L13.4854 7.99933L9.24269 12.242M1.66669 8H13.0984" strokeWidth="1.5" />
                        </svg>
                    </div>
                </div>
            </div >
        )
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(Component);
