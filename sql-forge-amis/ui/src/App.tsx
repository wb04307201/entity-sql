import 'amis/lib/themes/cxd.css';
import 'amis/lib/helper.css';
import 'amis/sdk/iconfont.css';
// 或 import 'amis/lib/themes/antd.css';

import ky from 'ky';

// 定义fetcher配置的类型
interface FetcherConfig {
    url: string; // 接口地址
    method: string; // 请求方法 get、post、put、delete
    data?: Record<string, unknown> | unknown; // 请求数据
    responseType?: string;
    config?: Record<string, unknown>; // 其他配置
    headers?: Record<string, string>; // 请求头
}

// amis 环境配置
const env = {
    // 下面三个接口必须实现
    fetcher: (config: FetcherConfig) => {
        console.log(config)

        if (config.method === 'get'){
          return ky.get(config.url, {
            searchParams: config.data,
            headers: config.headers,
          }).json
        }else if (config.method === 'post'){
          return ky.post(config.url, {
            json: config.data,
            headers: config.headers,
          })
        }else if (config.method === 'put'){
          return ky.put(config.url, {
            json: config.data,
            headers: config.headers,
          })
        }else if (config.method === 'delete'){
          return ky.delete(config.url, {
            json: config.data,
          })
        }
    },
}

function App() {

  return (
    <>
        <div>Hello</div>
    </>
  )
}

export default App
