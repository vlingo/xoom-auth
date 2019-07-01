describe("When a new constraint is added to a permission", function () {

    var permissionData
    var newConstraint

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listPermissions")

        cy.fixture("existingPermission").then((permission) => {
            permissionData = permission

            cy.searchListFor("#permissions-list", permissionData.name)
            cy.get("#permissions-list").contains(permissionData.description).click()

            cy.fixture("newPermission").then((newPermission) => {
                newConstraint = newPermission.constraint

                cy.get("#permissions-list").get("#constraint-list").contains("edit").click()
                cy.enterConstraintDetails(newConstraint, ".v-dialog--active")

                cy.get(".v-dialog--active").contains("Save").click()
            })
        })

        cy.wait(1500)

    })   
    
    it("then changed constraint can be found in the constraint list.", () => {
        cy.get("#constraint-list").should("contain", newConstraint.name)
    })
    
    it("then the old constraint data can not be found.", () => {
        cy.get("#constraint-list").should("not.contain", permissionData.constraint.name)
    })
})
