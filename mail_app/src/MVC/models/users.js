const { Schema, model, Types } = require('mongoose');

const userSchema = new Schema({
  firstName: { type: String, required: true, trim: true },
  lastName:  { type: String, required: true, trim: true },
  birthday:  { type: Date, required: true, trim: true },
  username:  { type: String, required: true, unique: true },
  password:  { type: String, required: true, trim: true },
  profileImage: { type: String },

  // Store default labels as references to Label documents
  defaultLabels: {
    inbox:   { type: Types.ObjectId, ref: 'Label' },
    sent:    { type: Types.ObjectId, ref: 'Label' },
    starred: { type: Types.ObjectId, ref: 'Label' },
    draft:   { type: Types.ObjectId, ref: 'Label' },
    spam:    { type: Types.ObjectId, ref: 'Label' },
    trash:   { type: Types.ObjectId, ref: 'Label' },
    read:    { type: Types.ObjectId, ref: 'Label' },
  },
}, {
  timestamps: true,
});

module.exports = model('User', userSchema);
