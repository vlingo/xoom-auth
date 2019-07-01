describe("When I logout after successful login ", function() {
    before(() => {
        
        cy.server()
        cy.route({
            method: "POST",
            url: '**/users/authentic',
            response: true
        })
        
        cy.standardLogin()
    })

    it("then I can see the login form.", function() {
        cy.get("#ApplicationFrame").get("#logoutButton").click()

        cy.get("#sign-on")
    })
})