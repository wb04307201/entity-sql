import React from 'react';

import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/css/v4-shims.css';

import 'amis/lib/themes/cxd.css';
import 'amis/lib/helper.css';
import 'amis/sdk/iconfont.css';
// 或 import 'amis/lib/themes/antd.css';

import {ToastComponent, AlertComponent} from 'amis';
import AMISComponent from "./AMISComponent";

function APP() {
    return (
        <>
            <ToastComponent key="toast" position={'top-right'}/>
            <AlertComponent key="alert"/>
            <AMISComponent
                page={{
                    type: 'page',
                    body: {
                        type: 'form',
                        api: '/api/form',
                        body: [
                            {
                                type: 'input-text',
                                name: 'name',
                                label: '姓名'
                            },
                            {
                                name: 'email',
                                type: 'input-email',
                                label: '邮箱'
                            },
                            {
                                name: 'color',
                                type: 'input-color',
                                label: 'color'
                            },
                            {
                                type: 'editor',
                                name: 'editor',
                                label: '编辑器'
                            }
                        ]
                    }
                }}
            />
        </>
    );
}

export default APP;
