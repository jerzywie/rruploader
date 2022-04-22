# rruploader


Doesn't upload anything! It reads data from a spreadsheet and transforms it

## Usage

Run the project directly, via `main-opts` (`-m rruploader.core`):

    $ clojure -M:run-m
    
Build an uberjar:

    $ clojure -X:uberjar
    
Run the uberjar:

    $ java -jar target/rruploader.jar [args]

Run tests

    $ clojure -X:test:runner

## Options

Run the program without arguments to see the usage message

## License

Copyright © 2022 jerzywie 

Distributed under the MIT Public License
