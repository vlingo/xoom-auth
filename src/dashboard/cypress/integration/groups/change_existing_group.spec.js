describe("When an existing group is changed", () => {
    var newGroupData
    var existingGroupData

    before(() => {
        cy.standardLogin()
        cy.visit("#/listGroups")

        cy.fixture("existingGroup").then((group) => {

            existingGroupData = group

            cy.searchListFor("#groups-list", existingGroupData.name)
            cy.get("#editGroupButton").click()
        })

    })

    it("then the name field is disabled.", () => {
        cy.get(".v-dialog--active").get("#nameField").should("be.disabled")
    })

    context("by changeing its description", () => {

        before(() => {
            cy.fixture("newGroup").then((newGroup) => {
                newGroup.name = undefined
                newGroupData = newGroup

                cy.enterGroupDetails(newGroup, ".v-dialog--active")
                cy.get(".v-dialog--active").contains("Save").click()
            })
        })

        it("then it can be found in the group list with its old name and new description.", () => {
            
            cy.searchListFor("#groups-list", existingGroupData.name)
            
            cy.get("#groups-list").within(() => {
                cy.get(".v-table").should("contain", existingGroupData.name)
                cy.get(".v-table").should("contain", newGroupData.description)
            })
        })
    })

})