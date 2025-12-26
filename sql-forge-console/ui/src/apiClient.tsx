import {Modal} from "antd";
import axios from 'axios';

// 创建实例
const apiClient = axios.create({
    // baseURL: 'https://api.example.com',
    timeout: 5000,
});

// 添加请求拦截器
apiClient.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
apiClient.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    console.log('response',response)
    return response.data;
}, function (error) {
    Modal.error({title: '错误', content: error.message || '未知错误'});
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
});

export default apiClient;