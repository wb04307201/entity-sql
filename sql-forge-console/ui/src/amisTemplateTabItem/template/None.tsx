import { forwardRef, useImperativeHandle } from 'react';
import type {AmisTemplateCrudMethods} from "../../type.tsx";

const None = forwardRef<AmisTemplateCrudMethods>((_, ref) => {
    const getContext = () => {
        return JSON.stringify({
            "type": "page"
        })
    };

    // 暴露方法给父组件
    useImperativeHandle(ref, () => ({
        getContext
    }));

    return <div>请选择模板</div>;
});

export default None;