/* jshint eqeqeq:false */
(function (window) {
    "use strict";

    var MemoryStorage = {};

    /**
     * Creates a new client side storage object and will create an empty
     * collection if no collection already exists.
     *
     * @param {string} name The name of our DB we want to use
     * @param {function} callback Our fake DB uses callbacks because in
     * real life you probably would be making AJAX calls
     */
    function Store(name, callback) {
        callback = callback || function () {};

        this._dbName = name;

        if (!MemoryStorage[name]) {
            fetch('/rest/todo/')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(res => {
                    var data = {
                        todos: res,
                    };
                    MemoryStorage[name] = JSON.stringify(data);
                })
                .catch(error => {
                    console.error('Error fetching data:', error); // Handle any errors during the fetch operation
                });
        } else {
            callback.call(this, JSON.parse(MemoryStorage[name]));
        }
    }

    /**
     * Finds items based on a query given as a JS object
     *
     * @param {object} query The query to match against (i.e. {foo: 'bar'})
     * @param {function} callback     The callback to fire when the query has
     * completed running
     *
     * @example
     * db.find({foo: 'bar', hello: 'world'}, function (data) {
     *     // data will return any items that have foo: bar and
     *     // hello: world in their properties
     * });
     */
    Store.prototype.find = function (query, callback) {
        if (!callback)
            return;

        var todos = JSON.parse(MemoryStorage[this._dbName]).todos;

        callback.call(
            this,
            todos.filter(function (todo) {
                for (var q in query) {
                    if (query[q] !== todo[q])
                        return false;
                }

                return true;
            })
        );
    };

    /**
     * Will retrieve all data from the collection
     *
     * @param {function} callback The callback to fire upon retrieving data
     */
    Store.prototype.findAll = function (callback) {
        callback = callback || function () {};
        callback.call(this, JSON.parse(MemoryStorage[this._dbName]).todos);
    };

    /**
     * Will save the given data to the DB. If no item exists it will create a new
     * item, otherwise it'll simply update an existing item's properties
     *
     * @param {object} updateData The data to save back into the DB
     * @param {function} callback The callback to fire after saving
     * @param {string} id An optional param to enter an ID of an item to update
     */
    Store.prototype.save = function (updateData, callback, id) {
        var data = JSON.parse(MemoryStorage[this._dbName]);
        var todos = data.todos;

        callback = callback || function () {};

        // If an ID was actually given, find the item and update each property
        if (id) {
            fetch('rest/todo/' + id, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updateData),
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(res => {
                    console.log('Item updated: ' + id);
                    for (var i = 0; i < todos.length; i++) {
                        if (todos[i].id === id) {
                            for (var key in res)
                                todos[i][key] = res[key];

                            break;
                        }
                    }

                    MemoryStorage[this._dbName] = JSON.stringify(data);
                    callback.call(this, todos);
                })
                .catch(error => {
                    console.error('Error creating item:', error);
                });
        } else {
            fetch('rest/todo/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updateData),
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(res => {
                    console.log('Item saved: ' + res.id);
                    todos.push(res);
                    MemoryStorage[this._dbName] = JSON.stringify(data);
                    callback.call(this, [res]);
                })
                .catch(error => {
                    console.error('Error creating item:', error);
                });
        }
    };

    /**
     * Will remove an item from the Store based on its ID
     *
     * @param {string} id The ID of the item you want to remove
     * @param {function} callback The callback to fire after saving
     */
    Store.prototype.remove = function (id, callback) {
        var data = JSON.parse(MemoryStorage[this._dbName]);
        var todos = data.todos;

        fetch('rest/todo/'+id, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                console.log('Itemo removed: ' + id);
                for (var i = 0; i < todos.length; i++) {
                    if (todos[i].id === id) {
                        todos.splice(i, 1);
                        break;
                    }
                }

                MemoryStorage[this._dbName] = JSON.stringify(data);
                callback.call(this, todos);
            })
            .catch(error => {
                console.error('Error removing item:', error);
            });
    };

    /**
     * Will drop all storage and start fresh
     *
     * @param {function} callback The callback to fire after dropping the data
     */
    Store.prototype.drop = function (callback) {
        fetch('rest/todo/', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                console.log('List cleared');
                var data = { todos: [] };
                MemoryStorage[this._dbName] = JSON.stringify(data);
                callback.call(this, data.todos);
            })
            .catch(error => {
                console.error('Error clearing items:', error);
            });
    };

    // Export to window
    window.app = window.app || {};
    window.app.Store = Store;
})(window);