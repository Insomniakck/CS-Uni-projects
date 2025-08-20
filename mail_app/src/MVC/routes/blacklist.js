const express = require('express')
const router = express.Router()
const controller = require('../controllers/blacklist')

router.route('/')
        .post(controller.addURL)

router.route('/:urlID')
        .delete(controller.deleteURL)
        
module.exports = router