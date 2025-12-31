import React, {useEffect, useMemo, useState} from 'react';

import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/css/v4-shims.css';

import 'amis/lib/themes/cxd.css';
import 'amis/lib/helper.css';
import 'amis/sdk/iconfont.css';
// 或 import 'amis/lib/themes/antd.css';

import {ToastComponent, AlertComponent} from 'amis';
import AMISComponent from './AMISComponent';
import axios from 'axios';
import {Schema} from 'amis-core/lib/types';

function APP() {
  const [id, setId] = useState<string | null>(null);
  const [page, setPage] = useState<Schema>();

  useEffect(() => {
    let params = new URL(document.location.href).searchParams;
    if (params.has('id'))
      setId(params.get('id'))
  }, []);

  useEffect(() => {
    if (id){
      axios.get(`/sql/forge/amis/template/${id}`).then(res => {
        setPage(JSON.parse(res.data.context));
      });
    }
  }, [id]);

  return (
    <>
      <ToastComponent key="toast" position={'top-right'} />
      <AlertComponent key="alert" />
      {page ? <AMISComponent page={page} />:<div>Loading...</div>}
    </>
  );
}

export default APP;
