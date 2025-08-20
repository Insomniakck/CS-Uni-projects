import * as mailAPI from "../APIfunctions/APIfunctions";


export async function getUserLabels( userID ) {
    const defaultLabelsObj = await mailAPI.getUserDefaultLabels(userID);
    const allLabels = await mailAPI.getLabelList(userID);

    if (!defaultLabelsObj || !allLabels) {
      console.error("Missing label data:", { defaultLabelsObj, allLabels });
      return [];
    }

    const defaultLabelIDs = Object.values(defaultLabelsObj);
    const customLabels = allLabels.filter(label => !defaultLabelIDs.includes(label._id));

    return customLabels;


}

export async function deleteLabel(labelID, userID){
    try {
        await fetch(`http://localhost:8080/api/labels/${labelID}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        });

        } catch (err) {
        console.error("Failed to delete label", err);
    }
}