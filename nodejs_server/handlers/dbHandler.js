/* dbHandler.js */

var mongodb = require("mongodb"),
	server  = new mongodb.Server("localhost", 27017, { auto_reconnect : true, poolSize : 10 }),
	db      = new mongodb.Db("secretChat", server, { w: 1 });
	collection = db.collection("members");

db.open();

exports.insertDb = function (contents, callback) {
    collection.insert(contents, function(err) {
        console.log("insert Data: ", JSON.stringify(contents));
        callback(err);
    });
};
	
exports.findDb = function (where, options, callback) {
	collection.find(where, options).toArray(function(err, data) {
		if (typeof data[0] == "undefined")
			err = 1;
		
        console.log("find data:", JSON.stringify(data[0]));
        callback(err, data[0]);
    });
};

exports.updateDb = function (where, operator, callback) {
    collection.update(where, operator, function(err, data) {
        console.log("update Data: ", data.result);
        callback(err);
    });
};

exports.removeDb = function (where, callback) {
    collection.remove(where, function (err, data) {
        console.log("remove Data: ", data.result);
        callback(err);
    });
};
