import { Component, OnInit } from "@angular/core";
import { Model, StylesManager } from "survey-core";
import { SurveyService } from "../_services/survey.service";
import { SurveyPDF, IDocOptions } from "survey-pdf";
import "survey-core/survey.i18n";

const exportToPdfOptions: IDocOptions = {
  fontSize: 12
};

const savePdf = function (surveyData: any, surveyJson: any) {
  const surveyPdf = new SurveyPDF(surveyJson, exportToPdfOptions);
  surveyPdf.data = surveyData;
  surveyPdf.raw().then((pdfData) => {
    const formData = new FormData();
    const surveyName = new Date().getTime() + "_survey_results.pdf";
    formData.append("survey", new Blob([pdfData], { type: "application/pdf" }), surveyName);

    // Send the PDF data to the server to save it to the database
    fetch("http://34.155.164.205/api/auth/survey/survey_result", {
      method: "POST",
      body: formData
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to save PDF file to database");
      }
      console.log("PDF file saved to database successfully");
    })
    .catch((error) => {
      console.error(error);
    });
  });
};

StylesManager.applyTheme("modern");

@Component({
  selector: "app-survey",
  templateUrl: "./survey.component.html",
  styleUrls: ["./survey.component.css"],
})


export class SurveyComponent implements OnInit {
  title = "My First Survey";
  surveyModel: Model;
  
  constructor(private surveyService: SurveyService){}

  logSurveyResults(sender: { data: any; }, surveyJson: any) {
    console.log(sender.data);
    savePdf(sender.data, surveyJson);
  }

  ngOnInit() {
    this.surveyService.getSurvey(5).subscribe((surveyJson) => {
      const survey = new Model(surveyJson);
      survey.locale = "fr";
      survey.onComplete.add((sender) => this.logSurveyResults(sender, surveyJson));
      this.surveyModel = survey;
    });

  }
}
