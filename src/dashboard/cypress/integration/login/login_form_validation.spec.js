describe("When login form is visible ", function() {
    var userData
    
    before(() => {
        cy.visit("/")
        
        cy.fixture("loginUser").then( (user) => {
            userData = user
        })
    })

    context("then login is not possible", function() {

        it("if tenant id is empty.", function() {

            cy.get("#tenantId").clear()
            cy.get("#username").clear().type("test user")
            cy.get("#credentialId").clear().type("test credential")
            cy.get("#secret").clear().type("secret")

            cy.get("#loginButton").should("be.disabled")
        })


        it("if username is empty.", function() {
            
            cy.get("#tenantId").clear().type("123456")
            cy.get("#username").clear()
            cy.get("#credentialId").clear().type("test credential")
            cy.get("#secret").clear().type("secret")

            cy.get("#loginButton").should("be.disabled")
        })

        it("if credential is empty.", function() {

            cy.get("#tenantId").clear().type("123456")
            cy.get("#username").clear().type("test user")
            cy.get("#credentialId").clear()
            cy.get("#secret").clear().type("secret")

            cy.get("#loginButton").should("be.disabled")
        })

        it("if secret is empty.", function() {

            cy.get("#tenantId").clear().type("123456")
            cy.get("#username").clear().type("test user")
            cy.get("#credentialId").clear().type("test credential")
            cy.get("#secret").clear()

            cy.get("#loginButton").should("be.disabled")
        })
    })

    context("and valid data is given", () => {

        it("then login is possible.", function() {
            cy.enterLoginData(userData, "123456")

            cy.get("#sign-on").get("#loginButton").should("be.enabled")
        })
    })
})