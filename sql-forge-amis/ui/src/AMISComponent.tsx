import {render} from 'amis';
import axios from "axios";
import copy from "copy-to-clipboard";
import {toast} from "amis-ui";
import {fetchOptions, fetcherResult} from "amis-core/lib/types";

interface AMISComponentProps {
    page: any;
    props: any;
    env: {
        fetcher: (config: fetchOptions) => Promise<fetcherResult>,
        isCancel: (value: any) => boolean,
        copy: (content: string) => void
    };
}

function AMISComponent(props: AMISComponentProps) {
    return render(
        props.page,
        props.props,
        props.env
    );
}

AMISComponent.defaultProps = {
    page: {},
    props: {},
    // amis 环境配置
    env: {
        // 下面三个接口必须实现
        fetcher: (config: fetchOptions) => {
            let {url, method, data, responseType, config: userConfig, headers} = config;

            let axiosConfig = userConfig || {};
            axiosConfig.withCredentials = true;
            responseType && (axiosConfig.responseType = responseType);

            if (axiosConfig.cancelExecutor) {
                axiosConfig.cancelToken = new (axios as any).CancelToken(
                    axiosConfig.cancelExecutor
                );
            }

            axiosConfig.headers = headers || {};

            const httpMethod = method?.toLowerCase() || 'get';

            if (httpMethod !== 'post' && httpMethod !== 'put' && httpMethod !== 'patch') {
                if (data) {
                    axiosConfig.params = data;
                }
                return (axios as any)[httpMethod](url, axiosConfig);
            } else if (data && data instanceof FormData) {
                axiosConfig.headers = axiosConfig.headers || {};
                axiosConfig.headers['Content-Type'] = 'multipart/form-data';
            } else if (
                data &&
                typeof data !== 'string' &&
                !(data instanceof Blob) &&
                !(data instanceof ArrayBuffer)
            ) {
                data = JSON.stringify(data);
                axiosConfig.headers = axiosConfig.headers || {};
                axiosConfig.headers['Content-Type'] = 'application/json';
            }

            return (axios as any)[httpMethod](url, data, axiosConfig);
        },
        isCancel: (value: any) => (axios as any).isCancel(value),
        copy: (content: string) => {
            copy(content);
            toast.success('内容已复制到粘贴板');
        }
    }
};

export default AMISComponent
