class InfoItemViewController: UIViewController {
  enum Style {
    case regular
    case link
  }

  typealias TapHandler = () -> Void

  @IBOutlet var imageView: UIImageView!
  @IBOutlet var infoLabel: UILabel!
  @IBOutlet var accessoryImage: UIImageView!
  @IBOutlet var separatorView: UIView!
  @IBOutlet var tapGestureRecognizer: UITapGestureRecognizer!

  var tapHandler: TapHandler?
  var style: Style = .regular {
    didSet {
      switch style {
      case .regular:
        imageView?.styleName = "MWMBlack"
        infoLabel?.styleName = "blackPrimaryText"
      case .link:
        imageView?.styleName = "MWMBlue"
        infoLabel?.styleName = "linkBlueText"
      }
    }
  }
  var canShowMenu = false

  @IBAction func onTap(_ sender: UITapGestureRecognizer) {
    tapHandler?()
  }

  @IBAction func onLongPress(_ sender: UILongPressGestureRecognizer) {
    guard sender.state == .began, canShowMenu else { return }
    let menuController = UIMenuController.shared
    menuController.setTargetRect(infoLabel.frame, in: self.view)
    infoLabel.becomeFirstResponder()
    menuController.setMenuVisible(true, animated: true)
  }

  override func viewDidLoad() {
    super.viewDidLoad()

    if style == .link {
      imageView.styleName = "MWMBlue"
      infoLabel.styleName = "linkBlueText"
    }
  }
}

protocol PlacePageInfoViewControllerDelegate: AnyObject {
  func didPressCall()
  func didPressWebsite()
  func didPressEmail()
}

class PlacePageInfoViewController: UIViewController {
  private struct Const {
    static let coordinatesKey = "PlacePageInfoViewController_coordinatesKey"
  }
  private typealias TapHandler = InfoItemViewController.TapHandler
  private typealias Style = InfoItemViewController.Style

  @IBOutlet var stackView: UIStackView!

  private lazy var openingHoursView: OpeningHoursViewController = {
    storyboard!.instantiateViewController(ofType: OpeningHoursViewController.self)
  }()

  private var rawOpeningHoursView: InfoItemViewController?
  private var phoneView: InfoItemViewController?
  private var websiteView: InfoItemViewController?
  private var emailView: InfoItemViewController?
  private var cuisineView: InfoItemViewController?
  private var operatorView: InfoItemViewController?
  private var wifiView: InfoItemViewController?
  private var addressView: InfoItemViewController?
  private var coordinatesView: InfoItemViewController?
  private var overlanderOpenView: InfoItemViewController?
  private var overlanderElectricityView: InfoItemViewController?
  private var overlanderWifiView: InfoItemViewController?
  private var overlanderKitchenView: InfoItemViewController?
  private var overlanderParkingView: InfoItemViewController?
  private var overlanderRestaurantView: InfoItemViewController?
  private var overlanderShowersView: InfoItemViewController?
  private var overlanderWaterView: InfoItemViewController?
  private var overlanderToiletsView: InfoItemViewController?
  private var overlanderBigrigfriendlyView: InfoItemViewController?
  private var overlanderTentfriendlyView: InfoItemViewController?
  private var overlanderPetfriendlyView: InfoItemViewController?
  private var overlanderSanidumpView: InfoItemViewController?
  

  var placePageInfoData: PlacePageInfoData!
  weak var delegate: PlacePageInfoViewControllerDelegate?
  var showFormattedCoordinates: Bool {
    get {
      UserDefaults.standard.bool(forKey: Const.coordinatesKey)
    }
    set {
      UserDefaults.standard.set(newValue, forKey: Const.coordinatesKey)
    }
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    _ = placePageInfoData

    if let openingHours = placePageInfoData.openingHours {
      openingHoursView.openingHours = openingHours
      addToStack(openingHoursView)
    } else if let openingHoursString = placePageInfoData.openingHoursString {
      rawOpeningHoursView = createInfoItem(openingHoursString, icon: UIImage(named: "ic_placepage_open_hours"))
      rawOpeningHoursView?.infoLabel.numberOfLines = 0
    }

    if let phone = placePageInfoData.phone {
      var cellStyle: Style = .regular
      if let phoneUrl = placePageInfoData.phoneUrl, UIApplication.shared.canOpenURL(phoneUrl) {
        cellStyle = .link
      }
      phoneView = createInfoItem(phone, icon: UIImage(named: "ic_placepage_phone_number"), style: cellStyle) { [weak self] in
        self?.delegate?.didPressCall()
      }
    }

    if let website = placePageInfoData.website {
      websiteView = createInfoItem(website, icon: UIImage(named: "ic_placepage_website"), style: .link) { [weak self] in
        self?.delegate?.didPressWebsite()
      }
    }

    if let email = placePageInfoData.email {
      emailView = createInfoItem(email, icon: UIImage(named: "ic_placepage_email"), style: .link) { [weak self] in
        self?.delegate?.didPressEmail()
      }
    }

    if let cuisine = placePageInfoData.cuisine {
      cuisineView = createInfoItem(cuisine, icon: UIImage(named: "ic_placepage_cuisine"))
    }

    if let ppOperator = placePageInfoData.ppOperator {
      operatorView = createInfoItem(ppOperator, icon: UIImage(named: "ic_placepage_operator"))
    }
    
    if (placePageInfoData.overlanderOpen != "No" && placePageInfoData.overlanderOpen != "" && placePageInfoData.overlanderOpen != nil) {
      let overlanderOpenString = L("open") + " - " + placePageInfoData.overlanderOpen!
      overlanderOpenView = createInfoItem(overlanderOpenString, icon: UIImage(named: "ic_placepage_overlander_open"))
    }
    
    if (placePageInfoData.overlanderElectricity != "No" && placePageInfoData.overlanderElectricity != "" && placePageInfoData.overlanderElectricity != nil) {
      let overlanderElectricityString = L("electricity") + " - " + placePageInfoData.overlanderElectricity!
      overlanderElectricityView = createInfoItem(overlanderElectricityString, icon: UIImage(named: "ic_placepage_overlander_electricity"))
    }
    
    if (placePageInfoData.overlanderWifi != "No" && placePageInfoData.overlanderWifi != "" && placePageInfoData.overlanderWifi != nil) {
      let overlanderWifiString = L("wifi") + " - " + placePageInfoData.overlanderWifi!
      overlanderWifiView = createInfoItem(overlanderWifiString, icon: UIImage(named: "ic_placepage_wifi"))
    }
    
    if (placePageInfoData.overlanderKitchen != "No" && placePageInfoData.overlanderKitchen != "" && placePageInfoData.overlanderKitchen != nil) {
      let overlanderKitchenString = L("kitchen") + " - " + placePageInfoData.overlanderKitchen!
      overlanderKitchenView = createInfoItem(overlanderKitchenString, icon: UIImage(named: "ic_placepage_overlander_kitchen"))
    }
    
    if (placePageInfoData.overlanderParking != "No" && placePageInfoData.overlanderParking != "" && placePageInfoData.overlanderParking != nil) {
      let overlanderParkingString = L("parking") + " - " + placePageInfoData.overlanderParking!
      overlanderParkingView = createInfoItem(overlanderParkingString, icon: UIImage(named: "ic_placepage_overlander_parking"))
    }
    
    if (placePageInfoData.overlanderRestaurant != "No" && placePageInfoData.overlanderRestaurant != "" && placePageInfoData.overlanderRestaurant != nil) {
      let overlanderRestaurantString = L("restaurant") + " - " + placePageInfoData.overlanderRestaurant!
      overlanderRestaurantView = createInfoItem(overlanderRestaurantString, icon: UIImage(named: "ic_placepage_overlander_restaurant"))
    }
    
    if (placePageInfoData.overlanderShowers != "No" && placePageInfoData.overlanderShowers != "" && placePageInfoData.overlanderShowers != nil) {
      let overlanderShowersString = L("showers") + " - " +  placePageInfoData.overlanderShowers!
      overlanderShowersView = createInfoItem(overlanderShowersString, icon: UIImage(named: "ic_placepage_overlander_showers"))
    }
    
    if (placePageInfoData.overlanderWater != "No" && placePageInfoData.overlanderWater != "" && placePageInfoData.overlanderWater != nil) {
      let overlanderWaterString = L("water") + " - " +  placePageInfoData.overlanderWater!
      overlanderWaterView = createInfoItem(overlanderWaterString, icon: UIImage(named: "ic_placepage_overlander_water"))
    }
    
    if (placePageInfoData.overlanderToilets != "No" && placePageInfoData.overlanderToilets != "" && placePageInfoData.overlanderToilets != nil) {
      let overlanderToiletsString = L("toilets") + " - " + placePageInfoData.overlanderToilets!
      overlanderToiletsView = createInfoItem(overlanderToiletsString, icon: UIImage(named: "ic_placepage_overlander_toilets"))
    }
    
    if (placePageInfoData.overlanderBigrigfriendly != "No" && placePageInfoData.overlanderBigrigfriendly != ""  && placePageInfoData.overlanderBigrigfriendly != nil) {
      let overlanderBigrigfriendlyString = L("big_rig_friendly") + " - " + placePageInfoData.overlanderBigrigfriendly!
      overlanderBigrigfriendlyView = createInfoItem(overlanderBigrigfriendlyString, icon: UIImage(named: "ic_placepage_overlander_bigrigfriendly"))
    }
    
    if (placePageInfoData.overlanderTentfriendly != "No" && placePageInfoData.overlanderTentfriendly != "" && placePageInfoData.overlanderTentfriendly != nil) {
      let overlanderTentfriendlyString = L("tent_friendly") + " - " + placePageInfoData.overlanderTentfriendly!
      overlanderTentfriendlyView = createInfoItem(overlanderTentfriendlyString, icon: UIImage(named: "ic_placepage_overlander_tentfriendly"))
    }
    
    if (placePageInfoData.overlanderPetfriendly != "No" && placePageInfoData.overlanderPetfriendly != "" && placePageInfoData.overlanderPetfriendly != nil) {
      let overlanderPetfriendlyString = L("pet_friendly") + " - " + placePageInfoData.overlanderPetfriendly!
      overlanderPetfriendlyView = createInfoItem(overlanderPetfriendlyString, icon: UIImage(named: "ic_placepage_overlander_petfriendly"))
    }

    if (placePageInfoData.overlanderSanidump != "No" && placePageInfoData.overlanderSanidump != "" && placePageInfoData.overlanderSanidump != nil) {
      let overlanderSanidumpString = L("sanidump") + " - " + placePageInfoData.overlanderSanidump!
      overlanderSanidumpView = createInfoItem(overlanderSanidumpString, icon: UIImage(named: "ic_placepage_overlander_sanidump"))
    }

    if placePageInfoData.wifiAvailable {
      wifiView = createInfoItem(L("WiFi_available"), icon: UIImage(named: "ic_placepage_wifi"))
    }

    if let address = placePageInfoData.address {
      addressView = createInfoItem(address, icon: UIImage(named: "ic_placepage_adress"))
      addressView?.canShowMenu = true
    }

    if let formattedCoordinates = placePageInfoData.formattedCoordinates,
      let rawCoordinates = placePageInfoData.rawCoordinates {
      let coordinates = showFormattedCoordinates ? formattedCoordinates : rawCoordinates
      coordinatesView = createInfoItem(coordinates, icon: UIImage(named: "ic_placepage_coordinate")) {
        [unowned self] in
        self.showFormattedCoordinates = !self.showFormattedCoordinates
        let coordinates = self.showFormattedCoordinates ? formattedCoordinates : rawCoordinates
        self.coordinatesView?.infoLabel.text = coordinates
      }
    } else if let formattedCoordinates = placePageInfoData.formattedCoordinates {
      coordinatesView = createInfoItem(formattedCoordinates, icon: UIImage(named: "ic_placepage_coordinate"))
    } else if let rawCoordinates = placePageInfoData.rawCoordinates {
      coordinatesView = createInfoItem(rawCoordinates, icon: UIImage(named: "ic_placepage_coordinate"))
    }

    coordinatesView?.accessoryImage.image = UIImage(named: "ic_placepage_change")
    coordinatesView?.accessoryImage.isHidden = false
    coordinatesView?.canShowMenu = true
  }

  // MARK: private

  private func createInfoItem(_ info: String,
                              icon: UIImage?,
                              style: Style = .regular,
                              tapHandler: TapHandler? = nil) -> InfoItemViewController {
    let vc = storyboard!.instantiateViewController(ofType: InfoItemViewController.self)
    addToStack(vc)
    vc.imageView.image = icon
    vc.infoLabel.text = info
    vc.style = style
    vc.tapHandler = tapHandler
    return vc;
  }

  private func addToStack(_ viewController: UIViewController) {
    addChild(viewController)
    stackView.addArrangedSubview(viewController.view)
    viewController.didMove(toParent: self)
  }
}
