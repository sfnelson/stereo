// ==========================================================================
// Stereo.Collection
// ==========================================================================

require('core');

/** @class

  (Document your class here)

  @extends SC.Record
  @author AuthorName
  @version 0.1
*/
Stereo.Collection = SC.Record.extend(
/** @scope Stereo.Collection.prototype */ {

  trackCount: function() {
    return this.get('tracks').get('length');
  }.property('tracks')

}) ;
