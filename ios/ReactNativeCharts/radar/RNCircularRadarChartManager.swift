//  Created by xudong wu on 23/02/2017.
//  Copyright wuxudong
//

import UIKit

@objc(RNCircularRadarChartManager)
@objcMembers
open class RNCircularRadarChartManager: RCTViewManager {
  override open func view() -> UIView! {
    let ins = RNCircularRadarChartView()
    return ins;
  }

  override public static func requiresMainQueueSetup() -> Bool {
    return true;
  }

}
