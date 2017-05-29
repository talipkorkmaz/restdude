import BaseAuthenticated  from '../../base-authenticated';
import SaveModelMixin from 'super-rentals/mixins/spaces/save-model-mixin';

export default BaseAuthenticated.extend( {
  beforeModel(transition) {
    this.transitionTo('spaces.space.settings');
  },
});
