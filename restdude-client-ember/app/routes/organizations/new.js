import Ember from 'ember';
import SaveModelMixin from 'super-rentals/mixins/organizations/save-model-mixin';

export default Ember.Route.extend(SaveModelMixin, {
  model: function() {
    return this.store.createRecord('organization');
  }
});