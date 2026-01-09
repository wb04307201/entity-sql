import React, {useEffect, useMemo, useState} from 'react';

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
