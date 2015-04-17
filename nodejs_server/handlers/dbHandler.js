/* dbHandler.js */

var mongodb = require('mongodb'),
	server  = new mongodb.Server('localhost', 27017, {}),
	db      = new mongodb.Db('secretChat', server, {w: 1}),
	collection = db.collection("members");
	
exports.insertDb = function (contents, callback) {
    db.open(function(err) {
        if (err) throw err;
        
        collection.insert(contents, function(err, data) {
            if (err) throw err;
            
            console.log("insert Data: ", data.result);
            console.log("insert Data: ", JSON.stringify(contents));
            db.close();
            callback(err);
        });
    });
};
	
exports.findDb = function (where, options, callback) {
	console.log("start db find!");
	db.open(function (err) {
        if (err) throw err;
        
        collection.find(where, options).toArray(function(err, data) {
            if (err) throw err;
            
            console.log("find data:", JSON.stringify(data[0]));
            // db.close();
            callback(err, data[0]);
        });
    });
};

exports.updateDb = function (where, operator, callback) {
	db.open(function(err) {
        if (err) throw err;
        
        collection.update(where, operator, function(err, data) {
            if (err) throw err;
            
            console.log("update Data: ", data.result);
            db.close();
            callback(err);
        });
    });
};

exports.removeDb = function (where, callback) {
    db.open(function (err) {
        if (err) throw err;
        
        collection.remove(where, function (err, data) {
            if (err) throw err;
            
            console.log("reve Data: ", data.result);
            db.close();
            callback(err);
        });
    });
};
