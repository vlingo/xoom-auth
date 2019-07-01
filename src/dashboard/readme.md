# vlingo-auth-ui

## First Steps

After downloading source do following steps to see all current features live. You will need to have internet connection and Chrome installed:

1. open a console and navigate to project folder
2. enter **npm install** to download all dependencies and to install test runner
3. enter **npm run test:integration** to start dev environment and test runner
4. click on **run all specs** (button on the top right side) tests for all working features are executed

## Commands

### Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Run cypress alone
```
npm run cypress
```
### Run integration test environment (dev environment and cypress)
```
npm run test:integration
```



## To-dos

- UI is not attached to vlingo-auth back-end API
  - in src/store you can find the stores. These contain multiple todos where to add the actual API calls. You should create an own service component an reference it within the stores, for the actual communication part.
- It is not possible to configure roles for groups or users.
- The current user can not change his profile data.
- Login and logout is not attached to back-end, thus there is no real authorization.
- Input validation just checks for some input boxes if there is any value. 

## Known Bugs

- Group members and sub groups
  - The group list contains a dialog to configure sub groups and members. 
    - The member dialog does not retrieve correct data.
    - It is possible to add a parent group as sub group.
    - The sub groups get some times mixed up between groups.

