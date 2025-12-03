# caio-exercise-restfulapi

This is a simple RestfulAPI made with Kotlin that allows for:
* Read all users (GET)
* Read specific users (providing their ID) (GET)
* Generate new random users (GET)
* Generate new users (providing the specific data) (POST)
* Update existing users providing only the data that must be updated (PATCH)
* Delete users (providing their ID) (DELETE)

To each operation there is a specific endpoint.

User data is stored in a PostgreSQL database.
