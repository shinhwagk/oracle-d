import { Component } from '@angular/core';
import { Http } from '@angular/http'
import { NavController, NavParams } from 'ionic-angular';

import 'rxjs/add/operator/toPromise';

@Component({
  selector: 'page-awr-sql',
  templateUrl: 'sql.html'
})
export class AwrSqlPage {
    constructor(public navCtrl: NavController, public navParams: NavParams, private http: Http) { }
}