import ky from 'ky';
import {Modal} from "antd";

const apiClient = ky.create({
    hooks: {
        // afterResponse: [
        //     async (_request, _options, response) => {
        //         console.log('【响应拦截】收到响应:', response.status, response.url);
        //
        //         if (!response.ok) {
        //             const errorData: { error: string } = await response.json();
        //             throw new Error(errorData.error || '请求失败');
        //         }
        //     },
        // ],
        beforeError: [
            (error) => {
                Modal.error({title: '错误', content: error.message || '未知错误'});
                return error;
            },
        ],
    },
});

export default apiClient;