import React, {useEffect, useState} from 'react';
import Console from './Console';
import View from './view/View';

function App() {
  const [id, setId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let params = new URL(document.location.href).searchParams;
    if (params.has('id')) {
      setId(params.get('id'));
    }
    setLoading(false);
  }, []);

  return (
    <>{loading ? <div>Loading...</div> : id ? <View id={id} /> : <Console />}</>
  );
}

export default App;
