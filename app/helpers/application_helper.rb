module ApplicationHelper
	# Use the Ruby content_for method to set the page title
	def page_title(title)
    content_for(:page_title) { title }
  end
end
