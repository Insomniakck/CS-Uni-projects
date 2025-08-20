const express = require('express')
const router = express.Router()
const controller = require('../controllers/mails')

router.route('/')
        .get(controller.getAllUserMails)
        .post(controller.createMail)

router.get('/search', controller.searchString);
router.get('/search/:query', controller.searchString);

router.route('/draft')
        .post(controller.createDraft)

router.route('/draft/:mailID')
        .patch(controller.editDraft)

router.route('/:mailID')
        .get(controller.getMail)
        .patch(controller.editMail)
        .delete(controller.deleteMail)

module.exports = router