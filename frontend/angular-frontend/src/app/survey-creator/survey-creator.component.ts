import { Component, OnInit } from "@angular/core";
import { SurveyCreatorModel, localization } from "survey-creator-core";
import { SurveyService } from "../_services/survey.service";
import "survey-creator-core/survey-creator-core.i18n";

const creatorOptions = {
  showLogicTab: true,
  isAutoSave: false,
};

localization.currentLocale = "fr";


const defaultJson = {
    title: "Votre avis compte !",
    description: "Veuillez exprimer ce que vous pensez de votre expérience?",
    pages: [
      {
        elements: [
          {
            name: "Name",
            title: "Entrez votre nom complet:",
            type: "text",
          },
          {
            name: "Feedback",
            title: "Depuis combien de temps votre proche est accompagné par DIVADOM ?",
            type: "comment",
          },
          {
            name: "OverallSatisfaction",
            title:"À quelle catégorie d’aidant appartenez-vous?",
            type: "radiogroup",
            isRequired: true,
            choices: [
              "Conjoint","Enfant","Ami","Voisin","Autre lien familial","Frère-soeur","Autre"
            ],
          },
          {
            name: "Feedback",
            title: "Vivez-vous avec votre proche ?",
            type: "comment",
          },
          {
            name: "Feedback",
            title: "Si non, à quelle distance de son logement vivez-vous?",
            type: "comment",
          },
          {
            name: "Feedback",
            title: "Aujourd’hui, voyez-vous un changement dans votre vie quotidienne dans le soutien apporté à votre proche grâce à DIVADOM. Si oui, expliquez-nous comment : ",
            type: "comment",
          },
          {
            name: "Quality",
            title: "Voici une liste de questions qui reflètent comment les gens se sentent parfois quand ils prennent soin d’un proche. Pour chaque énoncé, cochez la case à laquelle il vous arrive de vous sentir ainsi : jamais, parfois, souvent. Il n’y a ni bonne, ni mauvaise réponse.",
            type: "matrix",
            isRequired: true,
            columns: [
              {
                value: 1,
                text: "Jamais",
              },
              {
                value: 2,
                text: "Parfois",
              },
              {
                value: 3,
                text: "Souvent",
              },
            ],
            rows: [
              {
                value: "Salary",
                text: "Le fait de vous occuper de votre proche entraîne-t-il des difficulté dans votre vie familiale?",
              },
              {
                value: "OverallBenefits",
                text: "Le fait de vous occuper de votre proche entraîne-t-il des difficulté dans vos relations avec vos amis, vos loisirs, ou dans votre travail?",
              },
              {
                value: "HealthBenefits",
                text: "Le fait de vous occuper de votre proche entraîne-t-il un retentissement sur votre santé(physique et/ou psychique)?",
              },
              {
                value: "PhysicalWorkEnvironment",
                text: "Avez-vous le sentiment de ne plus reconnaître votre parent?",
              },
              {
                value: "TrainingOpportunities",
                text: "Avez-vous peur pour l'avenir de votre parent?",
              },
              {
                value: "WorkingTimeFlexibility",
                text: "Souhaitez-vous être aidé(e) pour vous occuper votre parent?",
              },
              {
                value: "4",
                text: "Ressentez-vous une charge en vous occupant de votre parent?",
              },
            ],
          },
        ],
      },
    ],
  };

@Component({
  selector: 'survey-creator-component',
  templateUrl: './survey-creator.component.html',
  styleUrls: ['./survey-creator.component.css']
})
export class SurveyCreatorComponent implements OnInit {
  surveyCreatorModel: SurveyCreatorModel;

  constructor(private surveyService: SurveyService){}

  ngOnInit() {
    const creator = new SurveyCreatorModel(creatorOptions);
    this.surveyService.getLatestSurvey().subscribe(
      (data: any) => {
        creator.text = JSON.stringify(data) || JSON.stringify(defaultJson);
      },
    );

    creator.saveSurveyFunc = (saveNo: number, callback: Function) => { 
      window.localStorage.setItem("survey-json", creator.text);
      callback(saveNo, true);
      saveSurveyJson(
          "http://34.155.164.205/api/auth/survey/save",
          creator.JSON,
          saveNo,
          callback
      );
    };
    this.surveyCreatorModel = creator;
  }
}



function saveSurveyJson(url: string | URL, json: object, saveNo: number, callback: Function) {
  const request = new XMLHttpRequest();
  request.open('POST', url);
  request.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
  request.addEventListener('load', () => {
      callback(saveNo, true);
  });
  request.addEventListener('error', () => {
      callback(saveNo, false);
  });
  request.send(JSON.stringify(json));
}