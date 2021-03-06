var dat = {
  "legal.search": {
    numCols: 4,
    title:"advocates.search.document.title.agency",
    useTimestamp: true,
    objectName: "",
    url: "/lcms-services/legalcase/advocate/agency/_search?isIndividual=false",
    groups: [
      {
        name: "applicantType",
        label: "advocates.create.group.title.agencySearch",
        fields: [
            {
            name: "agencOrganizationName",
            jsonPath: "agencyName",
            label: "advocates.create.agencOrganizationName",
            pattern: "",
            type: "text",
            isRequired: false,
            isDisabled: false,
            requiredErrMsg: "",
            patternErrMsg: ""
          }
        ]
      }
    ],
    "result": {
       "disableRowClick" : true,
       isAction: true,
          actionItems: [
            {
              label: "Update Advocate",
              url: "/update/legal/advocate/"
            }
          ],
      "header": [
         {
          label: "legal.search.result.actionLabels",
          isChecked:true,
          checkedItem:{
            jsonPath:"checkedRow",
            label:"",
          }
        },{
           "label": "legal.search.result.agencyName"
        },
        {
            "label": "legal.search.result.agencyAddress"
        }
      ],
      "values": [
        "code",
        "name",
        "agencyAddress"
      ],
      "resultPath": "agencies",
      //"rowClickUrlUpdate": "/update/legalcase/{id}",
      //"rowClickUrlView": "/view/legalcase/{id}"
    }
  }
};
export default dat;

