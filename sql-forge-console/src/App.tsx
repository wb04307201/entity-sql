import { Layout } from 'antd';

const { Header, Content, Footer, Sider } = Layout;
function App() {

  return (
      <Layout style={{height: '100%'}}>
              <Sider theme={'light'}>left sidebar</Sider>
              <Content>main content</Content>
      </Layout>
  )
}

export default App
