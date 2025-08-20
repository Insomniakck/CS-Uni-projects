const { Schema, model, Types } = require('mongoose');

const labelSchema = new Schema(
    {
        labelName: {type: String, required: true, trim: true,},  
        username: { type: String, required: true, index: true, },
        mails: [{ type: Types.ObjectId, ref: 'Mail',},],
        
    },{ timestamps: true }
);

module.exports = model('Label', labelSchema);
