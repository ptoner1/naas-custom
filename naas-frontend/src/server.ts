import { initNodeFederation } from '@softarc/native-federation-node';

(async () => {

  await initNodeFederation({
    relBundlePath: './dist/naas-frontend/browser/'
  });
  
  await import('./bootstrap-server');

})();
