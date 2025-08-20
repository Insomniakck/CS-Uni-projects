const express = require('express');
const router = express.Router();
const controller = require('../controllers/users');
const upload = require('../middleware/multer'); 

router.route('/')
    .post(upload.single('profileImage'), controller.createUser);

router.route('/username/:username')
        .get(controller.getUserInfo)

router.route('/:id')
    .get(controller.getUserByID);

router.route('/folder')
    .post(controller.getUserDefaultLabels);

router.route('/folderID')
    .post(controller.getUserDefaultLabelsByName);

module.exports = router;
