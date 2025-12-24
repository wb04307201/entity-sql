import ky from 'ky';
import {Modal} from "antd";

const apiClient = ky.create({
    hooks: {
        beforeError: [
            (error) => {
                Modal.error({title: '错误', content: error.message || '未知错误'});
                return error;
            },
        ],
    },
});

export default apiClient;