import Ember from 'ember';
import SaveModelMixin from 'super-rentals/mixins/issue-comments/save-model-mixin';

export default Ember.Route.extend(SaveModelMixin, {
  model: function() {
    return this.store.createRecord('issue-comment');
  }
});
