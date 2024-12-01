# shadow-stack MVP

A minimum viable project setup using

- [shadow-cljs](https://github.com/thheller/shadow-cljs)
- [shadow-grove](https://github.com/thheller/shadow-grove)
- [shadow-css](https://github.com/thheller/shadow-css)

and whatever else I come up with in the future.

## Running
Clone this repo and to start developing you have two options.

### CLJ

Nothing in the used libraries actually uses anything from NPM, so you have the option to skip anything related to it and just run:

```
clj -M:dev:start
```

### NPM

If you want to use some NPM packages later, or generally do not mind using npm, run

```
npm install
npm start
```


## Ready to go

The above will start the `:app` build defined in `shadow-cljs.edn` via the `repl/start` function defined [here](https://github.com/thheller/shadow-stack-mvp/blob/main/src/dev/repl.clj). It will also start building the CSS via `shadow-css`.

Startup should look something like

```
shadow-cljs - config: .../shadow-stack-mvp/shadow-cljs.edn
shadow-cljs - starting via "clojure"
shadow-cljs - HTTP server available at http://localhost:5001
shadow-cljs - server version: 2.28.6 running at http://localhost:9630
shadow-cljs - nREPL server started on port 56987
[:app] Configuring build.
[:app] Compiling ...
[:app] Build completed. (196 files, 195 compiled, 0 warnings, 5.82s)
```

Once completed you can open http://localhost:5001 to view the "app".

## Making Release Builds

You can create a release build from the UI running at http://localhost:9630/builds/app or from the command line

### CLJ

```
clj -M:dev:release
```

### NPM

```
npm run release
```