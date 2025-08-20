const { Schema, model, Types } = require('mongoose');

const mailSchema = new Schema(
  {
    sender:   { type: String, required: true, trim: true },
    receiver: { type: String, default: '' },
    subject:  { type: String, default: '' },
    content:  { type: String, default: '' },
    date:     { type: Date,   default: Date.now },
  }, { timestamps: true }
);

module.exports = model('Mail', mailSchema);
