import React, {useEffect, useState} from 'react';

import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/css/v4-shims.css';

import 'amis/lib/themes/cxd.css';
import 'amis/lib/helper.css';
import 'amis/sdk/iconfont.css';
// 或 import 'amis/lib/themes/antd.css';

import {ToastComponent, AlertComponent} from 'amis';
import AMISComponent from "./AMISComponent";
import axios from 'axios';

function APP() {
    
    const [data, setData] = useState({
        "type": "page",
        "body": {
            "type": "spinner",
            "show": true
        }
    });

    useEffect( () => {
        let params = new URL(document.location.href).searchParams;
        let id = params.get('id');
        axios.get(`/sql/forge/amis/template/${id}`).then(res => {
            console.log('res',res)
            setData(JSON.parse(res.data.context))
        })
    })

    return (
        <>
            <ToastComponent key="toast" position={'top-right'}/>
            <AlertComponent key="alert"/>
            <AMISComponent page={data}/>
        </>
    );
}

export default APP;
