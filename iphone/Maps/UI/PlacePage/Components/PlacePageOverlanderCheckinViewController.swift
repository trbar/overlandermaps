class OverlanderCheckinViewController: UIViewController {
  @IBOutlet var dateAndAuthorTextView: UITextView!
  @IBOutlet var commentTextView: UITextView!

  override func viewDidLoad() {
    super.viewDidLoad()

    dateAndAuthorTextView.textContainerInset = .zero
    commentTextView.textContainerInset = .zero
  }
}

class PlacePageOverlanderCheckinViewController: UIViewController {

  @IBOutlet var stackView: UIStackView!

  private var checkin1View: OverlanderCheckinViewController?
  private var checkin2View: OverlanderCheckinViewController?
  private var checkin3View: OverlanderCheckinViewController?

  var placePageInfoData: PlacePageInfoData!
  weak var delegate: PlacePageInfoViewControllerDelegate?

  override func viewDidLoad() {
    _ = placePageInfoData
    
    if (placePageInfoData.overlanderCheckin1 != nil && placePageInfoData.overlanderCheckin1 != "") {
      let checkin1ArrayElements = placePageInfoData.overlanderCheckin1!.components(separatedBy: "|")
      let checkin1DateAndAuthor = "(" + checkin1ArrayElements[2] + "/5) " + checkin1ArrayElements[1] + " on " + checkin1ArrayElements[0]
      checkin1View = createCheckinItem(dateAndAuthor: checkin1DateAndAuthor, comment: checkin1ArrayElements[3])
    }
    
    if (placePageInfoData.overlanderCheckin2 != nil && placePageInfoData.overlanderCheckin2 != "") {
      let checkin2ArrayElements = placePageInfoData.overlanderCheckin2!.components(separatedBy: "|")
      let checkin2DateAndAuthor = "(" + checkin2ArrayElements[2] + "/5) " + checkin2ArrayElements[1] + " on " + checkin2ArrayElements[0]
      checkin1View = createCheckinItem(dateAndAuthor: checkin2DateAndAuthor, comment: checkin2ArrayElements[3])
    }
    
    if (placePageInfoData.overlanderCheckin3 != nil && placePageInfoData.overlanderCheckin3 != "") {
      let checkin3ArrayElements = placePageInfoData.overlanderCheckin3!.components(separatedBy: "|")
      let checkin3DateAndAuthor = "(" + checkin3ArrayElements[2] + "/5) " + checkin3ArrayElements[1] + " on " + checkin3ArrayElements[0]
      checkin3View = createCheckinItem(dateAndAuthor: checkin3DateAndAuthor, comment: checkin3ArrayElements[3])
    }
  }
  // MARK: private

  private func createCheckinItem(dateAndAuthor: String,
                              comment: String) -> OverlanderCheckinViewController {
    let vc = storyboard!.instantiateViewController(ofType: OverlanderCheckinViewController.self)
    addToStack(vc)
    let font = UIFont.regular14()
    let color = UIColor.blackPrimaryText()
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.lineSpacing = 4

    let attributedDateAndAuthorString: NSAttributedString
    attributedDateAndAuthorString = NSAttributedString(string: dateAndAuthor as String,
                                            attributes: [NSAttributedString.Key.font : font,
                                                          NSAttributedString.Key.foregroundColor: color,
                                                          NSAttributedString.Key.paragraphStyle: paragraphStyle])
    
    let attributedCommentString: NSAttributedString
    attributedCommentString = NSAttributedString(string: comment as String,
                                            attributes: [NSAttributedString.Key.font : font,
                                                          NSAttributedString.Key.foregroundColor: color,
                                                          NSAttributedString.Key.paragraphStyle: paragraphStyle])
    vc.dateAndAuthorTextView.attributedText = attributedDateAndAuthorString
    vc.commentTextView.attributedText = attributedCommentString
    return vc;
  }

  private func addToStack(_ viewController: UIViewController) {
    addChild(viewController)
    stackView.addArrangedSubview(viewController.view)
    viewController.didMove(toParent: self)
  }
}
