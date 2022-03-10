protocol SearchActivitiesCategoriesViewControllerDelegate: AnyObject {
  func activitiesCategoriesViewController(_ viewController: SearchActivitiesCategoriesViewController,
                                didSelect category: String)
}

final class SearchActivitiesCategoriesViewController: MWMTableViewController {
  private weak var delegate: SearchActivitiesCategoriesViewControllerDelegate?
  private let categories: [String]

  init(frameworkHelper: MWMSearchFrameworkHelper, delegate: SearchActivitiesCategoriesViewControllerDelegate?) {
    self.delegate = delegate
    categories = frameworkHelper.searchActivitiesCategories()
    super.init(nibName: nil, bundle: nil)
  }
  
  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    tableView.registerNib(cellClass: SearchCategoryCell.self)
    tableView.separatorStyle = .none
    tableView.keyboardDismissMode = .onDrag
  }
  
  override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return categories.count
  }
  
  override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let cell = tableView.dequeueReusableCell(cell: SearchCategoryCell.self, indexPath: indexPath)
    cell.update(with: category(at: indexPath))
    return cell
  }
  
  override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    let selectedCategory = category(at: indexPath)
    delegate?.activitiesCategoriesViewController(self, didSelect: selectedCategory)
  }
  
  func category(at indexPath: IndexPath) -> String {
    let index = indexPath.row
    return categories[index]
  }
}
