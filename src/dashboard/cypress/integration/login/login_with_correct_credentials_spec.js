describe("When I login with correct login data ", function () {
    before(() => {
        cy.standardLogin()
    })

    it("then I can see the actual application.", function () {
        cy.get("#ApplicationFrame")
    })
})