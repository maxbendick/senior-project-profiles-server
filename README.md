# senior-project-profiles-server

A Clojure library designed to act as the backend for the Vertible web app.

## Setup

If you don't have the JDK, install it: http://www.oracle.com/technetwork/java/javase/downloads/index.html.

If you don't have Leiningen, install it: https://leiningen.org/#install.

Because we are using Service Account Credentials to access the Google API, you must have the private key JSON file and do one of the following:
 * Store it anywhere, and set an environment variable (GOOGLE\_APPLICATION\_CREDENTIALS) to point to it
 * On Mac/Linux, rename and move the JSON file to be ~/.config/gcloud/application\_default\_credentials.json (making directories as needed)
 * On Windows, rename and move the JSON file to be %APPDATA%\gcloud\application\_default\_credentials.json (making directories as needed)
 
 _Instructions taken from https://github.com/SparkFund/google-apps-clj#using-service-account-credentials._

## Usage

From the home directory of the repository, run ````lein repl````.

In the repl, run ````(use 'senior-project-profiles-server.core :reload)````.

Now you can try running requests against our app like:

````(app-routes {:request-method :get :uri "/"})````

where you can change "/" to any path.


## Running locally with the client side

Set up the [client side project](https://github.com/maxbendick/SeniorProjectProfilesApp) if you haven't already.

From the root of the client side project, run: 

````npm run build````

Copy the contents of the newly created ````dist```` folder to ````<path to this project>/resources/public/````, creating directories as needed.

Run ````lein ring server```` and it should open up a browser to a locally-served version of the app.

## License

Copyright © 2017 Vertible

FIXME
Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
