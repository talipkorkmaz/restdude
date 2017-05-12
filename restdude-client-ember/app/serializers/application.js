// app/serializers/application.js
import Ember from 'ember';
import DS from 'ember-data';
export default DS.JSONAPISerializer.extend({
    /*keyForAttribute: function(key) {
        return Ember.String.decamelize(key);
    },*/
    keyForAttribute(key) { return key; },
    keyForRelationship: function(key) {
        return Ember.String.decamelize(key);
    }
});
