/* eslint-disable react/prop-types */
import React from "react";
import { connect } from "react-redux";
import './index.less';
import BaseHeader from './BaseHeader';
import BaseFooter from './BaseFooter';
const mapStateToProps = state => ({

});
class Component extends React.Component {
    constructor(props) {
        super(props)
    }
    render() {
        return (<div className="web3go-layout-page">
            <BaseHeader />
            <div class="container-content">
                <div class="content">
                    {this.props.children}
                </div>
            </div>
            <BaseFooter />
        </div>)

    }

}


export default connect(mapStateToProps)(Component);
