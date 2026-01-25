import React, {useState} from 'react';
import {Button, Modal, Select} from 'antd';
import {DictJoinInfo, JoinInfo} from '../../type';
import {SettingOutlined} from '@ant-design/icons';
import apiClient from '../../apiClient';

const ColumnRenderJoin = (props: {
  value: JoinInfo;
  index: number;
  data: any[];
  setData: (data: any[]) => void;
}) => {
  const [show, setShow] = useState(false);
  const [joinInfo, setJoinInfo] = useState<JoinInfo>(props.value);
  const [dictOptions, setDictOptions] = useState();

  const renderTitle = () => {
    return (
      <div>
        {props.value?.joinType === 'dict' && (
          <div>字典：{props.value?.dict}</div>
        )}
      </div>
    );
  };

  const loadDictOptions = async () => {
    const data = await apiClient.post('sql/forge/api/json/select/SYS_DICT', {
      '@column': ['DICT_CODE', 'DICT_NAME']
    });

    setDictOptions(
      data.map(item => ({
        value: item.DICT_CODE,
        label: item.DICT_NAME
      }))
    );
  };

  return (
    <div>
      {renderTitle()}
      <Button
        shape="circle"
        icon={<SettingOutlined />}
        size="small"
        onClick={() => {
          setShow(true);
        }}
      />
      <Modal
        title="关联信息"
        open={show}
        onCancel={() => setShow(false)}
        onOk={() => {
          const newData = [...props.data];
          newData[props.index].join = {...joinInfo};
          props.setData(newData);
          setShow(false)
        }}
      >
        <Select
          value={props.value?.joinType}
          options={[{value: 'dict', label: '字典'}]}
          onChange={async value => {
            setJoinInfo({joinType: value});
            await loadDictOptions();
          }}
        />
        {joinInfo?.joinType === 'dict' && (
          <Select
            placeholder="请选择字典"
            value={props.value?.dict}
            onChange={value => {
              setJoinInfo({...joinInfo, dict: value} as DictJoinInfo);
            }}
            options={dictOptions}
          />
        )}
      </Modal>
    </div>
  );
};

export default ColumnRenderJoin;
