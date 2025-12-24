import ky from 'ky';
import copy from 'copy-to-clipboard';
import {toast} from "amis-ui";
import {AlertComponent, render, ToastComponent} from "amis";
import type {FetcherConfig} from "amis-core/lib/factory";

import 'amis/lib/themes/cxd.css';
import 'amis/lib/helper.css';
import 'amis/sdk/iconfont.css';
// 或 import 'amis/lib/themes/antd.css';

// amis 环境配置
const env = {
    // 下面三个接口必须实现
    fetcher: (config: FetcherConfig) => {
        return ky[config.method](config.url, {}).json()
    },
    isCancel: (error: {name: string}) => error.name === 'AbortError',
    copy: (content: string) => {
        copy(content);
        toast.success('内容已复制到粘贴板');
    }
}

function AMISComponent(){

    return render(
        // 这里是 amis 的 Json 配置。
        {
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
        },
        {
            // props...
        },
        env
    )
}

function App() {

  return (
    <>
        <ToastComponent key="toast" position={'top-right'} />
        <AlertComponent key="alert" />
        <AMISComponent />
    </>
  )
}

export default App
