namespace Duck.Cameras.Windows.Model
{
    public class SettingsUrl
    {
        public SettingsUrl(string value)
        {
            Value = value;
        }

        public string Value { get; private set; }

        public bool IsEmpty()
        {
            return string.IsNullOrEmpty(Value);
        }
    }
}
