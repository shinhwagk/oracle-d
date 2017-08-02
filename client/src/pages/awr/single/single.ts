import { Component } from '@angular/core';
import { Http } from '@angular/http'
import { NavController, NavParams } from 'ionic-angular';

import 'rxjs/add/operator/toPromise';

@Component({
  selector: 'page-awr-single',
  templateUrl: 'single.html'
})
export class AwrSinglePage {

  constructor(public navCtrl: NavController, public navParams: NavParams, private http: Http) { }

  getServers() {
    return ["yali", "yali2"]
  }

  public ss: AwrSnapshot[] = []

  a = false
  info: AwrSnapshot
  name
  displaySnapshots(server, days = 1) {
    const url = `http://10.65.103.15:8100/v1/awr/single/snapshots?name=${server}&days=${days}`
    return this.http.get(url)
      .toPromise()
      .then(response => {
        this.name = server
        this.ss = response.json()
        this.a = true
        this.info = this.ss[0]
      })
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

  reportAwr(sid, eid) {
    const url = `http://10.65.103.15:8100/v1/awr/single/report?name=${this.name}&dbid=${this.info.DBID}&instnum=1&bid=${sid}&eid=${eid}&mode=html`
    window.open(url)
  }

}

interface AwrSnapshot {
  SNAP_ID: number
  SNAPDAT: string
  DBID: number
  INST_NAME: string
}
