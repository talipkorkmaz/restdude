// app/adapters/application.js
import Ember from "ember";
import DS from "ember-data";
import DataAdapterMixin from "ember-simple-auth/mixins/data-adapter-mixin";
import config from "../config/environment";

export default DS.JSONAPIAdapter.extend(DataAdapterMixin, {

  host: `${config.host}`,
  namespace: `${config.namespace}`,
  authorizer: `${config.authorizer}`,
  corsWithCredentials: true,
  headers: {
    'Accept': 'application/vnd.api+json;charset=UTF-8',
    'Content-type': 'application/vnd.api+json;charset=UTF-8',
   // 'x-vendor-appid': '123',
   // 'x-vendor-secret': '12345'
  },
  ajax: function(url, type, hash) {
    console.log("ajax custom headers");
    if (this.headers !== undefined) {
      var headers = this.headers;
      if (hash) {
        hash.beforeSend = function (xhr) {
          Ember.keys(headers).forEach(function(key) {
            xhr.setRequestHeader(key, headers[key]);
          });
        };
      }
    }
    return this._super(url, type, hash);
  },

  // TODO: description  and param documentation
  /**
   * This method overrides DataAdapterMixin.urlForQueryRecord() to...
   *
   * @augments DataAdapterMixin
   * @param query
   * @returns {string}
   */
  urlForQueryRecord(query) {
    if (query.me) {
      delete query.me;
      return `${this._super(...arguments)}/me`;
    }

    return this._super(...arguments);
  }

});
