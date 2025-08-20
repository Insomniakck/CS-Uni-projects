const express = require('express')
const router = express.Router()
const controller = require('../controllers/labels')

router.route('/')
        .get(controller.getAllLabels)
        .post(controller.createLabel)

router.route('/:labelID')
        .get(controller.getLabel)
        .patch(controller.editLabel)
        .delete(controller.deleteLabel)

router.route('/mail')
        .post(controller.addMailToLabel)
router.route('/mail/remove')
        .post(controller.removeMailFromLabel)
router.route('/mail/:labelID')
        .get(controller.getMailsForLabel)
        
module.exports = router