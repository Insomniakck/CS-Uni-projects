const multer = require('multer');
const path = require('path');

// Storage config
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        const ext = path.extname(file.originalname);
        const uniqueName = `${Date.now()}-${Math.round(Math.random() * 1E9)}${ext}`;
        cb(null, uniqueName);
    }
});

// File filter (optional: restrict image types)
const fileFilter = (req, file, cb) => {
    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg'];
    cb(null, allowedTypes.includes(file.mimetype));
};

const upload = multer({ storage, fileFilter });

module.exports = upload;
