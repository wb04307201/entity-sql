import React, {useEffect, useState} from 'react';
import {ToastComponent, AlertComponent} from 'amis';
import AMISComponent from './AMISComponent';
import {Schema} from 'amis-core/lib/types';
import apiClient from '../apiClient';

function View({id}: {id: string}) {
  const [page, setPage] = useState<Schema>();

  useEffect(() => {
    if (id) {
      apiClient.get(`/sql/forge/amis/template/${id}`).then(res => {
        setPage(JSON.parse(res.context));
      });
    } else {
      setPage(undefined);
    }
  }, [id]);

  return (
    <>
      <ToastComponent key="toast" position={'top-right'} />
      <AlertComponent key="alert" />
      {page ? <AMISComponent page={page} /> : <div>Loading...</div>}
    </>
  );
}

export default View;
